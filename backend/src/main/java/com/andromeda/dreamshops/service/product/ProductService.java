package com.andromeda.dreamshops.service.product;

import com.andromeda.dreamshops.dto.CategoryDto;
import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.dto.ProductDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.*;
import com.andromeda.dreamshops.repository.*;
import com.andromeda.dreamshops.request.*;
import com.andromeda.dreamshops.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;
    private final ShopRepository shopRepository;
    private final ICategoryService categoryService;
    /**
     * @param  request request to add product
     * @param shopId id of the shop
     * @return the saved product
     * @throws AlreadyExistsException if a product with the same name and brand already exists
     */
    @Override
    public Product addProduct(AddProductRequest request, Long shopId) {
        //check if the category is found in the database
        //if yes, set it as new product category
        //if no , then save it as a new category
        //then set it as the new product category

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(()-> new ResourceNotFoundException("Shop not found with id: " + shopId));

        if(isProductExists(request.getName(), shop.getName())){
            throw new AlreadyExistsException("Product already exists with name: " + request.getName() + " in shop: " + shop.getName()
                    + " , you may update this product instead.");
        }

        Category category = categoryService.resolveCategory(request.getCategory());

        request.setCategory(category);
        return productRepository.save(createProduct(request, category, shop));
    }

    private boolean isProductExists(String name, String shopName) {
        return productRepository.existsByNameAndShopName(name, shopName);
    }

    private Product createProduct(AddProductRequest request, Category category, Shop shop) {

        Product product = new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
        product.setShop(shop);
        return product;
    }

    /**
     * @param id of the product
     * @return the product
     */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    /**
     * @param id of the product
     * @param shopId id of the shop
     */
    @Override
    public void deleteProductById(Long id, Long shopId) {
        productRepository.findByIdAndShopId(id, shopId)
                .ifPresentOrElse(productRepository::delete,
                        ()-> {throw new ResourceNotFoundException("Product not found with id: " + id + " in shop with id: " + shopId);});
    }

    /**
     * @param request to update product
     * @param productId of the product to be updated
     * @param shopId id of the shop
     */
    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId, Long shopId) {
        return productRepository.findByIdAndShopId(productId, shopId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(()-> new ResourceNotFoundException("Product not Found!"));
    }

    private Product updateExistingProduct(Product existingProduct,
                                          ProductUpdateRequest request){
        Optional.ofNullable(request.getName()).ifPresent(existingProduct::setName);
        Optional.ofNullable(request.getBrand()).ifPresent(existingProduct::setBrand);
        Optional.ofNullable(request.getPrice()).ifPresent(existingProduct::setPrice);
        Optional.ofNullable(request.getInventory()).ifPresent(existingProduct::setInventory);
        Optional.ofNullable(request.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(request.getCategory()).ifPresent(categoryRequest->{
            Category category = categoryService.resolveCategory(request.getCategory());
            existingProduct.setCategory(category);
        });

        return existingProduct;
    }

    /**
     * @return all products
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * @param category name of the category of which products we want
     * @return a list of the products
     */
    @Override
    public List<Product> getAllProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    /**
     * @param brand of the products to be fetched
     * @return list of products
     */
    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    /**
     * @param name of the products to be fetched
     * @return list of products
     */
    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * @param brand of the products
     * @param name of the products
     * @return list of products
     */
    // Ex. get all products of brand Samsung and name Galaxy S21
    // as in all products of brand Samsung having name Galaxy S21
    // sold by all the shops in the platform
    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    /**
     * @param category of the products
     * @param brand of the products
     * @return list of products
     */
    // Ex. get all products of category Phones and brand Apple
    @Override
    public List<Product> getProductByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    /**
     * @param brand of the products
     * @param name of the products
     * @return count of products
     */
    // How many products are there of brand Samsung and name Galaxy S21
    @Override
    public Long countProductByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    // if i want to get all products under Electronics category
    // it should return all products under Electronics and its sub-categories
    // which may be Laptops, Mobiles, Televisions etc.
    // there is an issue here product has category - Tv , Phone, Laptop etc
    // category has parentCategory - Electronics
    // products necessarily cannot be fetched under electronics
    // Alternatively we can get all the sub categories of electronics
    // then fetch all products under those sub-categories
    @Override
    public List<Product> getAllProductsByParentCategory(String parentCategoryName) {
        List<Category> subCategories = categoryService.getAllSubCategoriesByParentName(parentCategoryName);
        return subCategories.stream()
                .flatMap(category -> productRepository.findByCategoryName(category.getName()).stream())
                .toList();
    }

    // shop related product methods

    @Override
    public Product getProductByShopIdAndProductId(Long shopId, Long productId) {
        return productRepository.findByIdAndShopId(productId, shopId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found in the specified shop!"));
    }

    @Override
    public List<Product> getAllProductsByShopId(Long shopId) {
        return productRepository.findByShopId(shopId);
    }

    @Override
    public List<Product> getAllProductsByShopName(String shopName) {
        return productRepository.findByShopName(shopName);
    }

    @Override
    public List<Product> getAllProductsByShopAndCategory(String shopName, String categoryName) {
        return productRepository.findByShopNameAndCategoryName(shopName, categoryName);
    }

    @Override
    public List<Product> getAllProductsByShopAndBrand(String shopName, String brand) {
        return productRepository.findByShopNameAndBrand(shopName, brand);
    }

    @Override
    public List<Product> getAllProductsByShopBrandAndCategory(String shopName, String brand, String categoryName) {
        return productRepository.findByShopNameAndBrandAndCategoryName(shopName, brand, categoryName);
    }

    @Override
    public Product getProductByShopNameAndProductName(String shopName, String productName) {
        return productRepository.findByShopNameAndName(shopName, productName)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found in the specified shop!"));
    }

    @Override
    public Long countProductsByShopId(Long shopId) {
        return productRepository.countByShopId(shopId);
    }

}
