package dev.hbgl.hhn.schattenbuchhaltung.service.dto.Ledger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import dev.hbgl.hhn.schattenbuchhaltung.domain.User;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class UserVM {

    public String id;

    public String firstName;

    public String lastName;

    public String email;

    public String imageUrl;

    public static UserVM fromEntity(User entity) {
        if (entity == null) {
            return null;
        }
        var vm = new UserVM();
        vm.id = entity.getId();
        vm.firstName = entity.getFirstName();
        vm.lastName = entity.getLastName();
        vm.email = entity.getEmail();
        vm.imageUrl = entity.getImageUrl();
        return vm;
    }
}
