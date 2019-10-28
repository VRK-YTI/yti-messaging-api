package fi.vm.yti.messaging.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "resource")
public class Resource {

    private String uri;
    private String application;
    private String type;
    private Set<User> users;

    @Id
    @Column(name = "uri", unique = true)
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Column(name = "application")
    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_resource",
        joinColumns = {
            @JoinColumn(name = "resource_uri", referencedColumnName = "uri", nullable = false, updatable = false) },
        inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false) })
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(final Set<User> users) {
        this.users = users;
    }
}
