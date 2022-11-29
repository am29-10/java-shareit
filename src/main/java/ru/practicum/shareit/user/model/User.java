package ru.practicum.shareit.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Column(name = "name")
    private String name;
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "указанный email не соответствует правильной структуре 'email'.")
    @Column(name = "email", unique = true)
    private String email;
    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "requestor")
    private List<ItemRequest> itemRequests = new ArrayList<>();
}
