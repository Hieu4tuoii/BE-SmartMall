package vn.hieu4tuoi.mapper;

import org.mapstruct.*;
import vn.hieu4tuoi.Security.JwtRespone;
import vn.hieu4tuoi.dto.request.user.EmployeeRequest;
import vn.hieu4tuoi.dto.respone.user.UserResponse;
import vn.hieu4tuoi.model.User;

@Mapper(
    componentModel = "spring"
)
public interface UserMapper {
    JwtRespone UserToJwtResponse(User user);
    UserResponse entityToResponse(User user);
    User employeeRequestToEntity(EmployeeRequest request);
    //update user
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUser(EmployeeRequest request, @MappingTarget User user);
}
