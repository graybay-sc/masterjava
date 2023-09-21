package ru.javaops.masterjava;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class MainXmlTest {

    @Test
    public void test() throws JAXBException, IOException {
        MainXml.main(new String[]{"masterjava"});
    }
}