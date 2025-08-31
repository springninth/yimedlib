// 修改后的 UserServiceImpl.java
package com.example.ymedlib.service.impl;

import com.example.ymedlib.model.User;
import com.example.ymedlib.repository.UserRepository;
import com.example.ymedlib.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        // 检查是否是新用户（没有ID）
        if (user.getId() == null) {
            // 检查用户名是否已存在
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new RuntimeException("用户名已存在: " + user.getUsername());
            }
            // 对新用户的密码进行BCrypt编码
            if (!isPasswordEncoded(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(User user) {
        // 更新用户时，如果密码发生变化且未编码，则进行编码
        if (user.getId() != null) {
            Optional<User> existingUser = userRepository.findById(user.getId());
            if (existingUser.isPresent()) {
                // 如果密码与现有密码不同且不是BCrypt格式，则编码新密码
                if (!existingUser.get().getPassword().equals(user.getPassword()) &&
                        !isPasswordEncoded(user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 辅助方法：检查密码是否已经是BCrypt编码格式
    private boolean isPasswordEncoded(String password) {
        // BCrypt编码的密码通常以$2a$、$2b$、$2y$开头，长度为60字符
        return password != null && password.length() == 60 &&
                (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}