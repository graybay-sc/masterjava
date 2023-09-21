package ru.javaops.masterjava;

import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class MainXmlStaxTest {

    @Test
    public void main() throws XMLStreamException, IOException {
        MainXmlStax.main(new String[]{"masterjava"});
    }
}