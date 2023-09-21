package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.GroupStatusType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.*;

public class MainXmlStax {

    public static void main(String[] args) throws IOException, XMLStreamException {
        String projectName = args[0];
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            Map<String, Set<User>> groupToUsers = null;
            Set<String> groups = null;
            while (reader.hasNext()) {
                int event = reader.next();
                if (groupToUsers != null && groups != null) {
                    break;
                }
                if (event == XMLEvent.START_ELEMENT) {
                    if ("Users".equals(reader.getLocalName())) {
                        groupToUsers = searchUserGroups(reader);
                    } else if ("Projects".equals(reader.getLocalName())) {
                        groups = searchProjectGroups(reader, projectName);
                    }
                }
            }
            if (groups != null && !groups.isEmpty() && groupToUsers != null && !groupToUsers.isEmpty()) {
                groups.stream()
                        .map(groupToUsers::get)
                        .flatMap(Collection::stream)
                        .distinct()
                        .sorted(Comparator.comparing(User::getName))
                        .forEach(System.out::println);
            }
        }
    }

    private static Map<String, Set<User>> searchUserGroups(XMLStreamReader reader) throws XMLStreamException {
        Map<String, Set<User>> groupToUsers = new HashMap<>();
        String currentUserName = null;
        String currentUserEmail = null;
        Set<String> currentUserGroups = new HashSet<>();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.END_ELEMENT) {
                if ("Users".equals(reader.getLocalName())) break;
                if ("User".equals(reader.getLocalName())) {
                    for (String currentUserGroup : currentUserGroups) {
                        groupToUsers.merge(
                                currentUserGroup,
                                new HashSet<>(Collections.singleton(new User(currentUserName, currentUserEmail))),
                                (oldUsers, newUsers) -> {
                                    oldUsers.addAll(newUsers);
                                    return oldUsers;
                                }
                        );
                    }
                    currentUserName = null;
                    currentUserEmail = null;
                    currentUserGroups = new HashSet<>();
                }
            } else if (event == XMLEvent.START_ELEMENT) {
                if ("fullName".equals(reader.getLocalName())) {
                    currentUserName = reader.getElementText();
                } else if ("Group".equals(reader.getLocalName())) {
                    currentUserGroups.add(reader.getElementText());
                } else if ("User".equals(reader.getLocalName())) {
                    currentUserEmail = reader.getAttributeValue(null, "email");
                }
            }
        }
        return groupToUsers;
    }

    private static Set<String> searchProjectGroups(XMLStreamReader reader, String project) throws XMLStreamException {
        Set<String> groupToProject = new HashSet<>();
        boolean projectFound = false;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.END_ELEMENT) {
                if ("Projects".equals(reader.getLocalName())) break;
                if ("Project".equals(reader.getLocalName())) {
                    if (projectFound) break;
                }
            } else if (event == XMLEvent.START_ELEMENT) {
                if ("Project".equals(reader.getLocalName())) {
                    String projectName = reader.getAttributeValue(null, "name");
                    if (projectName.equals(project)) {
                        projectFound = true;
                    }
                } else if (projectFound && "Group".equals(reader.getLocalName())) {
                    if (GroupStatusType.CURRENT.name().equals(reader.getAttributeValue(null, "status"))) {
                        groupToProject.add(reader.getAttributeValue(null, "name"));
                    }
                }
            }
        }
        return groupToProject;
    }

    private static class User {

        private final String name;
        private final String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return name.equals(user.name) && email.equals(user.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }

        @Override
        public String toString() {
            return "User{" +
                   "name='" + name + '\'' +
                   ", email='" + email + '\'' +
                   '}';
        }
    }
}
