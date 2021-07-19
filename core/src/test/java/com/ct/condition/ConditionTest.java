package com.ct.condition;

import com.ct.condition.core.Conditions;
import org.junit.*;
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
public class ConditionTest {

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
    public void testAnd() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .eq("id", 2L)
                .and(i->i.eq("username", "lisi"))
                .and()
                .eq("realname", "李四")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testOr() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .eq("id", 2L)
                .or(i->i.eq("username", "lisi"))
                .or()
                .eq("realname", "张三")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testLike() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .rightLike("username", "lis")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
        spec = Conditions.use(User.class)
                .leftLike("username", "isi")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
        spec = Conditions.use(User.class)
                .allLike("username", "is")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }


    @Test
    public void testIn() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .in("id", 1,2)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());

        spec = Conditions.use(User.class)
                .in("id", Arrays.asList(1,2))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());

        spec = Conditions.use(User.class)
                .in("username", Arrays.asList("lisi","zhangsan"))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());


        spec = Conditions.use(User.class)
                .notIn("username", Arrays.asList("lisi","zhangsan"))
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());


        spec = Conditions.use(User.class)
                .notIn("username", "lisi","zhangsan")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testBetween() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .between("id", 2,4)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.use(User.class)
                .notBetween("id", 2,3)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }


    @Test
    public void testNull() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .isNull("realname")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());

        spec = Conditions.use(User.class)
                .isNotNull("realname")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testCompare() throws Exception {
        Specification<User> spec = Conditions.use(User.class)
                .ge("id",1)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());


        spec = Conditions.use(User.class)
                .le("id",1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());


        spec = Conditions.use(User.class)
                .lt("id",1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());

        spec = Conditions.use(User.class)
                .gt("id",1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.use(User.class)
                .notEq("id",1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());

        spec = Conditions.use(User.class)
                .eq("id",1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }
} 
