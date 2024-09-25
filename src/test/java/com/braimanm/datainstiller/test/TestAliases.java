package com.braimanm.datainstiller.test;

import com.braimanm.datainstiller.context.DataContext;
import com.braimanm.datainstiller.data.DataAliases;
import com.braimanm.datainstiller.data.GlobalAliases;
import com.braimanm.datainstiller.data.Person;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestAliases {

    @Parameters({"personData"})
    @Test
    public void testLocalVsGlobalAliases(@Optional("person_data_1.xml") String dataSet) {
        Person person = new Person().fromResource(dataSet);
        GlobalAliases global = DataContext.getGlobalAliases();
        DataAliases local = person.getDataAliases();
        Assert.assertEquals(global.resolveAliases(person.fullName), local.resolveAliases(person.fullName));
        global.put("firstName", "Momo");
        Assert.assertNotEquals(global.resolveAliases(person.fullName), local.resolveAliases(person.fullName));
        Assert.assertEquals(global.resolveAliases(person.fullName), local.resolveAliases("Momo " + person.lastName));
        String todayDate = DateTimeFormatter.BASIC_ISO_DATE.format(LocalDateTime.now());
        Assert.assertEquals(todayDate, global.evaluateExpression(person.todayDate));
    }

    @Parameters({"personData"})
    @Test(dependsOnMethods = {"testLocalVsGlobalAliases"})
    public void testLocalVsGlobalAliases2(@Optional("person_data_1.xml") String dataSet) {
        GlobalAliases global = DataContext.getGlobalAliases();
        Assert.assertEquals(global.resolveAliases("${firstName}"), "Momo");
        Person person = new Person().fromResource(dataSet);
        DataAliases local = person.getDataAliases();
        Assert.assertEquals(global.resolveAliases(person.fullName), local.resolveAliases(person.fullName));
    }

    @Test
    public void testContext() {
        GlobalAliases ga = DataContext.getGlobalAliases();
        ga.put("One", 1);
        ga.put("Two", 2);
        ga.put("Three", "${One + Two}");
        Object actual = ga.evaluateExpression("${One} + ${Two} = ${Three}");
        String expected = "1 + 2 = 3";
        Assert.assertEquals(actual, expected);
        ga.put("Nine", "${Three * Three}");
        actual = ga.evaluateExpression("${Nine}");
        int expectedInt = 9;
        Assert.assertEquals(actual, expectedInt);
    }

}
