package itemsharing.user;

import itemsharing.exception.DuplicatedDataException;
import itemsharing.exception.NotFoundException;
import itemsharing.user.dao.UserRepository;
import itemsharing.user.dto.UserDto;
import itemsharing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.toUserDtoCollection(userRepository.findAll());
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicatedDataException("User with e-mail = " + userDto.getEmail() + "exist!");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User (id = " + userId + ") not found!");
        });
        if (userDto.getEmail() != null
                && !userDto.getEmail().isBlank()) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicatedDataException("User with e-mail = " + userDto.getEmail() + "exist!");
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null
                && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User id = " + userId + " not found!");
        }));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
