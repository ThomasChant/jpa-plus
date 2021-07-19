package com.ct.condition;

import com.ct.condition.core.Conditions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/** 
* @description AbstractWrapper Tester. 
* @author ThomasChan
* @since <pre>7月 16, 2021</pre> 
* @version 1.0 
*/
@SpringBootTest(classes = App.class)
@RunWith(value = SpringRunner.class)
public class LambdaConditionTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        userRepository.saveAll(Arrays.asList(
                new User(1L, "zhangsan","张三"),
                new User(2L, "lisi","李四"),
                new User(3L, "wangwu","王五"),
                new User(4L, "zhaoliu",null)
        ));
    }

    @After
    public void destory() {
        userRepository.deleteAll();
    }

    @Test
    public void testAnd() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .eq(User::getId, 2L)
                .and(i->i.eq(User::getUsername, "lisi"))
                .and()
                .eq(User::getRealname, "李四")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testOr() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .eq(User::getId, 2L)
                .or(i->i.eq(User::getUsername, "lisi"))
                .or()
                .eq(User::getRealname, "张三")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testLike() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .rightLike(User::getUsername, "lis")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
        spec = Conditions.lambdaUse(User.class)
                .leftLike(User::getUsername, "isi")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
        spec = Conditions.lambdaUse(User.class)
                .allLike(User::getUsername, "is")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }


    @Test
    public void testIn() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .in(User::getId, 1,2)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());

        spec = Conditions.lambdaUse(User.class)
                .in(User::getId, Arrays.asList(1,2))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());

        spec = Conditions.lambdaUse(User.class)
                .in(User::getUsername, Arrays.asList("lisi","zhangsan"))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());


        spec = Conditions.lambdaUse(User.class)
                .notIn(User::getUsername, Arrays.asList("lisi","zhangsan"))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());


        spec = Conditions.lambdaUse(User.class)
                .notIn(User::getUsername, "lisi","zhangsan")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testBetween() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .between(User::getId, 2,4)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.lambdaUse(User.class)
                .notBetween(User::getId, 2,3)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }


    @Test
    public void testNull() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .isNull(User::getRealname)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());

        spec = Conditions.lambdaUse(User.class)
                .isNotNull(User::getRealname)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testCompare() {
        Specification<User> spec = Conditions.lambdaUse(User.class)
                .ge(User::getId,1)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());


        spec = Conditions.lambdaUse(User.class)
                .le(User::getId,1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());


        spec = Conditions.lambdaUse(User.class)
                .lt(User::getId,1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());

        spec = Conditions.lambdaUse(User.class)
                .gt(User::getId,1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.lambdaUse(User.class)
                .notEq(User::getId,1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.lambdaUse(User.class)
                .eq(User::getId,1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }
} 
