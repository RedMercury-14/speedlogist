package by.base.main.dto;

import java.util.Objects;

/**
 * Для фронта где изображения base64
 */
public class ImageDTO {
	
	private String name;
    private String contentType;
    private String base64;
    
    public ImageDTO(String name, String contentType, String base64) {
        this.name = name;
        this.contentType = contentType;
        this.base64 = base64;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	@Override
	public int hashCode() {
		return Objects.hash(base64, contentType, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageDTO other = (ImageDTO) obj;
		return Objects.equals(base64, other.base64) && Objects.equals(contentType, other.contentType)
				&& Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "ImageDTO [name=" + name + ", contentType=" + contentType + ", base64=" + base64 + "]";
	}
    
    

}
