package com.ct.condition;

import com.ct.condition.core.Conditions;
import com.ct.condition.core.JpaPlusException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** 
* @description AbstractWrapper Tester. 
* @author ThomasChan
* @since <pre>7月 16, 2021</pre> 
* @version 1.0 
*/
@SpringBootTest(classes = App.class)
@RunWith(value = SpringRunner.class)
@ActiveProfiles("test")
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

    @Test
    public void testEq() throws Exception {

        Specification spec = Conditions.use(User.class)
                .eq("realname", "")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());

        spec = Conditions.use(User.class)
                .eq("id", 1)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());

        spec = Conditions.use(User.class)
                .eq("id", 5)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());

        spec = Conditions.use(User.class)
                .eq("realname", null)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }

    @Test
    public void testAnd() throws Exception {
        Specification spec = Conditions.use(User.class)
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
        Specification spec = Conditions.use(User.class)
                .or()
                .eq("id", 2L)
                .or(i->i.eq("username", "wangwu"))
                .or()
                .eq("realname", "张三")
                .or()
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testOr1() throws Exception {
        Specification spec = Conditions.use(User.class)
                .eq("id", 1L)
                .or(i->i.eq("username", "lisi"))
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testOrAnd() throws Exception {
        Specification spec = Conditions.use(User.class)
                .or()
                .eq("id", 1)
                .or(i->i.eq("id", 2))
                .and()
                .eq("username", "zhangsan")
                .or()
                .eq("username", "zhaoliu")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }


    @Test
    public void testNestOr() throws Exception {

        // (id = 2 and username = 'lisi') and id == 1 or (id == 3 and username != 'zhaoliu') and username != 'zhaoliu'
        Specification spec = Conditions.use(User.class)
                .or(i->i.eq("id", 2).notEq("username","lisi"))
                .and()
                .eq("id", 1)
                .or(i->i.eq("id", 3).notEq("username","zhaoliu"))
                .eq("username", "zhaoliu")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(2, all.size());
    }


    @Test
    public void testNestAnd() throws Exception {
        Specification spec = Conditions.use(User.class)
                .and()
                .and(i->i.eq("id", 2).notEq("username","lisi"))
                .and()
                .eq("id", 1)
                .and()
                .and(i->i.eq("id", 3).notEq("username","zhaoliu"))
                .eq("username", "zhaoliu")
                .and()
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());
    }

    @Test
    public void testNestAndOr() throws Exception {
        Specification spec = Conditions.use(User.class)
                .and(i->i.eq("id", 1).or().notEq("id",2))
                .eq("id", 1)
                .or(i->i.eq("id", 3).and().notEq("username","zhaoliu"))
                .eq("username", "zhaoliu")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testLike() throws Exception {
        Specification spec = Conditions.use(User.class)
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

        spec = Conditions.use(User.class)
                .allLike("username", "")
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());

        spec = Conditions.use(User.class)
                .allLike("username", null)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }


    @Test
    public void testIn() throws Exception {
        Specification spec = Conditions.use(User.class)
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
    public void testCompare() throws Exception {
        Specification spec = Conditions.use(User.class)
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

    @Test
    public void testBetween(){
        Specification spec = Conditions.use(User.class)
                .between("id",1,3)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }


    @Test
    public void testNotBetween(){
        Specification spec = Conditions.use(User.class)
                .notBetween("id",1,3)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testNull(){
        Specification spec = Conditions.use(User.class)
                .isNull("realname")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testNotNull(){
        Specification spec = Conditions.use(User.class)
                .isNotNull("realname")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testValNull(){
        Specification spec = Conditions.use(User.class)
                .eq("id",null)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }

    @Test
    public void testValBlank(){
        Specification spec = Conditions.use(User.class)
                .eq("realname","")
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());

        spec = Conditions.use(User.class)
                .eq("realname",null)
                .toSpec();
        all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }

    @Test
    public void testInNull(){
        Specification spec = Conditions.use(User.class)
                .in("id",null, 1)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }

    @Test(expected = JpaPlusException.class)
    public void testEmptyCollection(){
        Specification spec = Conditions.use(User.class)
                .in("id", new ArrayList<Integer>())
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(0, all.size());
    }

    @Test(expected = JpaPlusException.class)
    public void testEmptyArray(){
        Specification spec = Conditions.use(User.class)
                .in("id", new Integer[]{})
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }

    @Test(expected = JpaPlusException.class)
    public void testAllNull(){
        Specification spec = Conditions.use(User.class)
                .in("id", null, null)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
    }

    @Test
    public void testInContainNull(){
        Specification spec = Conditions.use(User.class)
                .in("realname","李四", null)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(1, all.size());
    }


    @Test
    public void testCollectionNull(){
        Collection a = null;
        Specification spec = Conditions.use(User.class)
                .in("realname", a)
                .toSpec();
        List<User> all = userRepository.findAll(spec);
        Assert.assertEquals(4, all.size());
    }
} 
