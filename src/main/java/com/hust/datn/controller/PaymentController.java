package com.hust.datn.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.datn.entity.Order;
import com.hust.datn.enums.OrderStatus;
import com.hust.datn.exception.InternalException;
import com.hust.datn.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Controller
public class PaymentController {
	@Autowired
	OrderRepository orderRepository;
	
	@GetMapping("/user/payment")
	public String index(String orderId, Model model) throws StripeException {
		Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));
		if(!order.isPresent())
			return "redirect:/user/order-preview";
		
		Stripe.apiKey = "sk_test_51GtnsjJG8N5EtzpV8upDLc6PZ9xhr8bQkQh6s8KcABXqyo5nq8wKxYgzjpgAwIrJc68aWdnrumMd7nR6bY4jO8u8000vBgM4Ok";

		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setCurrency("vnd").setAmount((long) order.get().getCost()).build();

		PaymentIntent intent = PaymentIntent.create(params);

		model.addAttribute("client_secret", intent.getClientSecret());
		model.addAttribute("orderId", order.get().getId());

		return "user/payment";
	}
	
	@PostMapping("/user/payment-success")
	@ResponseBody
	public void paymentSuccess(String orderId) throws InternalException {
		Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));
		if(!order.isPresent())
			throw new InternalException("Không tìm thấy đơn hàng");
		
		Order od = order.get();
		od.setStatus(OrderStatus.PAID);
		
		orderRepository.save(od);
	}
}