package com.yrol.blog.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String username;
    private String email;
    private String password;

    /**
     * @ManyToMany -  Users can have many Roles vise-versa
     * EAGER loading since we need Roles available upfront as soon as we load a user.
     * CascadeType.ALL - to persist child object along with parent object (ex: when we delete a user a role will also get deleted in the JoinTable table)
     * joinColumns - User ID as user_id
     * inverseJoinColumns - Role ID as role_id
     *
     * */
    @ManyToMany(fetch =  FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
}
