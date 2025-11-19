package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//
//public class orderServiceImpl implements OrderService{
//
//    @Autowired
//    CartRepository cartRepository;
//    @Autowired
//    AddressRepository addressRepository;
//    @Autowired
//    PaymentRepository paymentRepository;
//    @Autowired
//    OrderRepository orderRepository;
//    @Autowired
//    OrderItemRepository orderItemRepository;
//    @Autowired
//    ProductRepository productRepository;
//    @Autowired
//    CartService cartService;
//    @Autowired
//    ModelMapper modelMapper;
//    @Autowired
//    @Override
//    @Transactional
//    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
//       Cart cart=cartRepository.findCartByEmail(emailId);
//       if(cart==null)
//       {
//           throw new ResourceNotFoundException("Cart","email",emailId);
//
//       }
//        Address address=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
//       Order order=new Order();
//       order.setEmail(emailId);
//       order.setOrderDate(LocalDate.now());
//       order.setTotalAmount(cart.getTotalPrice());
//       order.setOrderStatus("Order Accepted");
//       order.setAddress(address);
//       Payment payment=new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName)
//                payment.setOrder(order);
//       payment=paymentRepository.save(payment);
//               order.setPayment(payment);
//        Order savedOrder=orderRepository.save(order);
//        List<CartItem> cartItems=cart.getCartItems();
//        if(cartItems.isEmpty())
//        {
//            throw new APIException("Cart is Empty");
//        }
//        List<OrderItems>orderItems=new ArrayList<>();
//        for( CartItem cartItem:cartItems)
//        {
//            OrderItems orderItems1=new OrderItems();
//            orderItems1.setProduct(cartItem.getProduct());
//            orderItems1.setQuantity(cartItem.getQuantity());
//            orderItems1.setDiscount(cartItem.getDiscount());
//            orderItems1.setOrderedProductPrice(cartItem.getProductPrice());
//            orderItems1.setOrder(savedOrder);
//            orderItems.add(orderItems1);
//
//        }
//        orderItemRepository.saveAll(orderItems);
//        cart.getCartItems().forEach(item ->{
//            int quantity=item.getQuantity();
//            Product product =item.getProduct();
//            product.setQuantity(product.getQuantity()-quantity);
//            productRepository.save(product);
//            cartService deleteProductFromCart(item.getProduct().getProductId(),cart.getCartId());
//        });
//
//        OrderDTO orderDTO
//
//
//
//
//
//        return null;
//    }
//}


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod,
                               String pgName, String pgPaymentId, String pgStatus,
                               String pgResponseMessage) {

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is Empty");
        }

        List<OrderItems> orderItemsList = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItems oi = new OrderItems();
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setDiscount(cartItem.getDiscount());
            oi.setOrderedProductPrice(cartItem.getProductPrice());
            oi.setOrder(savedOrder);
            orderItemsList.add(oi);
        }

        orderItemRepository.saveAll(orderItemsList);

        // update product stock and remove items from cart
        cartItems.forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });
            OrderDTO orderDTO=modelMapper.map(savedOrder,OrderDTO.class);
        orderItemsList.forEach(item-> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;

    }
}

