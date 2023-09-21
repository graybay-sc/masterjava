package ru.javaops.masterjava.xml.util;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class UserTableUtilsTest {

    @Test
    public void testName() throws JAXBException, IOException {
        UserTableUtils.createHtmlUserTable();
    }

    @Test
    public void xslt() throws Exception {
        UserTableUtils.createHtmlXsltGroupTable("topjava");
    }
}