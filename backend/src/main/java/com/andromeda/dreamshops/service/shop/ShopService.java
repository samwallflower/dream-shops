package com.andromeda.dreamshops.service.shop;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.dto.ProductDto;
import com.andromeda.dreamshops.dto.ShopDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.model.Role;
import com.andromeda.dreamshops.model.Shop;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.repository.ProductRepository;
import com.andromeda.dreamshops.repository.RoleRepository;
import com.andromeda.dreamshops.repository.ShopRepository;
import com.andromeda.dreamshops.repository.UserRepository;
import com.andromeda.dreamshops.request.AddShopRequest;
import com.andromeda.dreamshops.request.UpdateShopRequest;
import com.andromeda.dreamshops.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService implements IShopService{
    private final ShopRepository shopRepository;
    private final IOrderService orderService;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public Shop addShop(AddShopRequest shop, Long userId) {
        //check if the shop with the same name already exists
        //so we don't have duplicate shop names
        //we should also check if the user already has a shop
        //if yes, then we should not allow to create another shop for the same user
        //as one user can have only one shop
        if(shopRepository.existsByShopOwnerId(userId)){
            throw new AlreadyExistsException("User already has a shop with userId: " + userId);
        }

        if(shopRepository.existsByName(shop.getName())){
            throw new AlreadyExistsException("Shop already exists with name: " + shop.getName());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + userId));
        Shop newShop = createShop(shop);
        newShop.setShopOwner(user);
        user.setShop(newShop);
        Role role = roleRepository.findByName("ROLE_SHOP_OWNER")
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_SHOP_OWNER"));

        if(!user.getRoles().contains(role))
            user.getRoles().add(role);
        //assign SHOP_OWNER role to the user only if the user doesn't have it already

        return shopRepository.save(newShop);
    }

    private Shop createShop(AddShopRequest request) {
        return new Shop(
                request.getName(),
                request.getAddress(),
                request.getContactNumber(),
                request.getContactEmail(),
                request.getDescription()
        );
    }

    @Override
    public Shop getShopByName(String name) {

        return shopRepository.findByName(name).orElseThrow(
                ()-> new ResourceNotFoundException("Shop not found with name: " + name)
        );
    }

    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Shop not found with id: " + id));
    }

    @Override
    public Shop updateShop(Long id, UpdateShopRequest shop) {
        // find the shop by id
        // if found update the shop details
        return shopRepository.findById(id)
                .map(existingShop -> UpdateExistingShop(existingShop, shop))
                .map(shopRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
    }

    private Shop UpdateExistingShop(Shop existingShop, UpdateShopRequest shop) {
        // here we need to check if the name already exists for another shop
        // first we check if the given name is different from the existing name
        // if different, then we check if the new name already exists in the database
        // if exists, then we throw an exception - shop name must be unique
        if (!existingShop.getName().equals(shop.getName())
                && shopRepository.existsByName(shop.getName())) {
            throw new AlreadyExistsException("Shop already exists with name: " + shop.getName());
        }
        Optional.ofNullable(shop.getName()).ifPresent(existingShop::setName);
        Optional.ofNullable(shop.getAddress()).ifPresent(existingShop::setAddress);
        Optional.ofNullable(shop.getContactNumber()).ifPresent(existingShop::setContactNumber);
        Optional.ofNullable(shop.getContactEmail()).ifPresent(existingShop::setContactEmail);
        Optional.ofNullable(shop.getDescription()).ifPresent(existingShop::setDescription);
        return existingShop;
    }

    @Override
    public void deleteShopById(Long id) {
        // check if shop exists
        // if it exists, delete it
        // if not, throw exception
        // also remove the shop reference from the user (shop owner)
        // and remove the SHOP_OWNER role from the user as well
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        Role role = roleRepository.findByName("ROLE_SHOP_OWNER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_SHOP_OWNER"));

        User shopOwner = shop.getShopOwner();
        if (shopOwner != null) {
            shopOwner.setShop(null);
            shopOwner.getRoles().removeIf(role::equals);
            userRepository.save(shopOwner);
        }
        shopRepository.deleteById(id);
    }

    @Override
    public Shop getShopByUserId(Long userId) {
        return shopRepository.findByShopOwnerId(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return shopRepository.existsByShopOwnerId(userId);
    }

    @Override
    public List<OrderDto> getOrdersByShopId(Long shopId) {
        return orderService.getOrdersByShopId(shopId);
    }

    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @Override
    public boolean existsByName(String shopName) {
        return shopRepository.existsByName(shopName);
    }

    @Override
    public Long countProductsInShop(Long shopId) {
        return productRepository.countByShopId(shopId);
    }

    @Override
    public ShopDto convertToDto(Shop shop) {
        ShopDto shopDto = modelMapper.map(shop, ShopDto.class);
        List<Product> products = productRepository.findByShopId(shop.getId());
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        shopDto.setProducts(productDtos);
        //orders
        List<OrderDto> orderDtos = orderService.getOrdersByShopId(shop.getId());
        shopDto.setOrders(orderDtos);
        return shopDto;
    }

    @Override
    public List<ShopDto> getConvertedShops(List<Shop> shops) {
        return shops.stream()
                .map(this::convertToDto)
                .toList();
    }
}
