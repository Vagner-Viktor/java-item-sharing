package itemsharing.user;

import itemsharing.user.dto.UserDto;
import itemsharing.user.model.User;

import java.util.Collection;

public class UserMapper {

    public static Collection<UserDto> toUserDtoCollection(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
