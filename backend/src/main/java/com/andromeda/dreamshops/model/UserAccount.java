package com.andromeda.dreamshops.model;

import com.andromeda.dreamshops.enums.AccountStatus;
import com.andromeda.dreamshops.enums.Gender;
import com.andromeda.dreamshops.enums.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@Table(name = "user_account")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Username should be unique
    private String username;

    private String profilePictureUrl;
    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // preferences

    private String dashboardColor = "#FFFFFF"; // default white

    @Enumerated(EnumType.STRING)
    private Theme preferredTheme = Theme.LIGHT;

    private String preferredLanguage = "en";

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.PENDING;


    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> savedAddresses;


    public UserAccount(String username, LocalDate dateOfBirth, Gender gender) {
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    // for demographic purposes
    public  Integer calculateAge(){
        if (dateOfBirth == null) return 0;
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

}
