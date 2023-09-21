package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class UserTableUtils {

    private UserTableUtils() {/**/}

    public static void createHtmlUserTable() throws IOException, JAXBException {
        JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
        jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload = jaxbParser.unmarshal(Resources.getResource("payload.xml").openStream());
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("title", "Users list");
        context.setVariable("users", payload.getUsers().getUser());
        StringWriter stringWriter = new StringWriter();
        templateEngine.process("user_table.html", context, stringWriter);
        System.out.println(stringWriter.toString());
    }

    public static void createHtmlXsltGroupTable(String projectName) throws Exception {
        try (InputStream xslInputStream = Resources.getResource("groups.xsl").openStream();
             InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {

            XsltProcessor processor = new XsltProcessor(xslInputStream, "project_name", projectName);
            System.out.println(processor.transform(xmlInputStream));
        }
    }
}
