package vn.hieu4tuoi.mapper;
import org.mapstruct.Mapper;
import vn.hieu4tuoi.dto.request.product.ImageRequest;
import vn.hieu4tuoi.dto.respone.product.ImageResponse;
import vn.hieu4tuoi.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image requestToEntity(ImageRequest request);
    ImageResponse entityToResponse(Image image);
}
