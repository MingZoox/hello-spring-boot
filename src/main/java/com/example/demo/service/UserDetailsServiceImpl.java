package com.example.demo.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${azure.storage.account-name}")
    private String AZURE_ACCOUNT_NAME;

    @Value("${azure.storage.account-key}")
    private String AZURE_STORAGE_ACCOUNT_KEY;

    @Value("${azure.storage.blob-endpoint}")
    private String AZURE_STORAGE_ENDPOINT;

    private String AZURE_CONTAINER_NAME = "test";

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetailsImpl(user);
    }

    public UserDTO getCurrentUserInformation() {
        UserDetailsImpl userDetails = (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUser().getUsername();
        User user = userRepository.findByUsername(userName);
        return modelMapper.map(user, UserDTO.class);
    }

    public void updateAvatar(MultipartFile file) throws IOException {
        UserDTO userDTO = getCurrentUserInformation();

        // upload avatar to azure storage
        String constr = "DefaultEndpointsProtocol=https" +
                ";AccountName=" + AZURE_ACCOUNT_NAME +
                ";AccountKey=" + AZURE_STORAGE_ACCOUNT_KEY +
                ";EndpointSuffix=core.windows.net";

        BlobContainerClient container = new BlobContainerClientBuilder()
                .connectionString(constr)
                .containerName(AZURE_CONTAINER_NAME)
                .buildClient();

        BlobClient blob = container.getBlobClient(userDTO.getUsername());
        blob.upload(file.getInputStream(), file.getSize(), true);
    }

    public User registrationUser(String username, String password) {
        UserDTO userDTO = getCurrentUserInformation();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User newUser = new User(username, bCryptPasswordEncoder.encode(password),AZURE_STORAGE_ENDPOINT + "/" + AZURE_CONTAINER_NAME + "/" + userDTO.getUsername());
        return userRepository.save(newUser);
    }
}
