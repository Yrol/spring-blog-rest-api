package com.yrol.blog.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * @ManyToMany -  Users can have many Roles vise-versa
     * EAGER loading since we need Roles available upfront as soon as we load a user.
     * CascadeType.ALL - to persist child object along with parent object (ex: when we delete a user a role will also get deleted in the JoinTable table)
     * joinColumns - User ID (from User) as user_id
     * inverseJoinColumns - Role ID (from Role) as role_id
     *
     * */
    @ManyToMany(fetch =  FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    /**
     * @OneToMany - Users can create many posts
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<Post>();
}
