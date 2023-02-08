package com.donations.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Nationalized;
import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Nationalized
	@Column(unique = true, length = 255, nullable = false)
	private String name;

	public Product() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Nationalized
	@Column(unique = true, length = 255, nullable = false)
	private String alias;

	@Nationalized
	@Column(length = 4096, nullable = false, name = "short_description")
	private String shortDescription;

	@Nationalized
	@Column(length = 8192, nullable = false, name = "full_description")
	private String fullDescription;

	@Column(name = "created_time", nullable = false, updatable = false)
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTimeDate;

	private boolean enabled = true;

	@Column(name = "in_stock")
	private boolean inStock = true;

	@Nationalized
	@Column(columnDefinition = "float default 0")
	private float cost;

	@Nationalized
	@Column(columnDefinition = "float default 0")
	private float price;

	@Column(name = "discount_percent", columnDefinition = "float default 0")
	private float discountPercent;

	@Column(columnDefinition = "float default 0")
	private float length;

	@Column(columnDefinition = "float default 0")
	private float width;

	@Column(columnDefinition = "float default 0")
	private float height;

	@Column(columnDefinition = "float default 0")
	private float weight;

	@Column(name = "main_image", nullable = false, columnDefinition = "nvarchar(1024) default 'default-images.png'")
	private String mainImage;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private Set<ProductImage> images = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTimeDate() {
		return updateTimeDate;
	}

	public void setUpdateTimeDate(Date updateTimeDate) {
		this.updateTimeDate = updateTimeDate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		return "Product [id = " + id + ", name = " + name + ", alia = " + alias + ", price = " + price + ", category = "
				+ category + ", brand = " + brand + "]";
	}

	public Set<ProductImage> getImages() {
		return images;
	}

	public void setImages(Set<ProductImage> images) {
		this.images = images;
	}

	public void addExtraImage(String imageName) {
		this.images.add(new ProductImage(imageName, this));
	}

	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null) {
			return "/images/default-image.png";
		}
		return "/product-images/" + this.id + "/" + this.mainImage;
	}

}
