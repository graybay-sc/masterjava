package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class MainXml {

    public static void main(String[] args) throws IOException, JAXBException {
        String projectName = args[0];
        JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
        jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload = jaxbParser.unmarshal(Resources.getResource("payload.xml").openStream());
        Set<String> groupNames = payload.getProjects().getProject().stream()
                .filter(project -> projectName.equals(project.getName()))
                .findFirst()
                .map(e -> e.getGroups().getGroup())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(group -> group.getStatus() == GroupStatusType.CURRENT)
                .map(Project.Groups.Group::getName)
                .collect(toSet());
        payload.getUsers().getUser().stream()
                .filter(user -> Optional.ofNullable(user.getGroups())
                        .map(User.Groups::getGroup)
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(JAXBElement::getValue)
                        .map(Project.Groups.Group.class::cast)
                        .map(Project.Groups.Group::getName)
                        .anyMatch(groupNames::contains))
                .sorted(Comparator.comparing(User::getFullName))
                .map(User::getFullName)
                .forEach(System.out::println);
    }
}
