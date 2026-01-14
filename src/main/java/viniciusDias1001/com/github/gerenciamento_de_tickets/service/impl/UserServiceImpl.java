package viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.UserSummaryResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.ForbiddenException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.NotFoundException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.UserRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.UserService;
import java.util.UUID;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getById(UUID requesterId, UUID userId) {
        User requester = getUserOrThrow(requesterId);


        if (requester.getRole() != UserRole.ADMIN && !requester.getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to access this user");
        }

        User user = getUserOrThrow(userId);
        return toSummary(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> list(UUID requesterId, UserRole role, Pageable pageable) {
        User requester = getUserOrThrow(requesterId);

        if (requester.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can list users");
        }

        Page<User> page = (role == null)
                ? userRepository.findAll(pageable)
                : userRepository.findByRole(role, pageable);

        return page.map(this::toSummary);
    }

    @Override
    public void delete(UUID requesterId, UUID userId) {
        User requester = getUserOrThrow(requesterId);

        if (requester.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete users");
        }

        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserSummaryResponse toSummary(User u) {
        return new UserSummaryResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
