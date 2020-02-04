package fi.vm.yti.messaging.entity;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "[user]")
public class User {

    private UUID id;
    private String subscriptionType;
    private Set<Resource> resources;

    @Id
    @Column(name = "id", unique = true)
    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    @Column(name = "subscription_type")
    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_resource",
        joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false) },
        inverseJoinColumns = {
            @JoinColumn(name = "resource_uri", referencedColumnName = "uri", nullable = false, updatable = false) })
    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(final Set<Resource> resources) {
        this.resources = resources;
    }
}
