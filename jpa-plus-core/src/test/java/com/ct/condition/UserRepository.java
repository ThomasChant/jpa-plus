package com.ct.condition;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 *
 * @author chentao
 * @date 2021/7/16
 */
@Repository
public interface UserRepository extends JpaRepositoryImplementation<User,Long> {
}
