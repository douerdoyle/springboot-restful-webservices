package net.javaguides.springboot.repository;

import net.javaguides.springboot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
