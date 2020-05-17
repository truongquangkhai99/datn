package com.hust.datn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hust.datn.command.SearchCommand;
import com.hust.datn.dto.CategoryDTO;
import com.hust.datn.entity.Category;
import com.hust.datn.repository.CategoryRepository;
import com.hust.datn.service.CategoryService;

@Controller
public class ProductController {
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/product")
	public String product(Model model) {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		return "product";
	}
	
	@GetMapping("/product/fetch-product")
	@ResponseBody
	public ModelAndView fetchProduct(@ModelAttribute SearchCommand command) {
		List<UUID> uuids = command.getUUIDs();
		List<CategoryDTO> dtos = new ArrayList<>();
		
		for (UUID uuid : uuids) {
			Optional<Category> optional = categoryRepository.findById(uuid);
			if(optional.isPresent()) {
				Category category = optional.get();
				Category filtered = category.filter(command.keyword, command.discount);
				dtos.add(categoryService.getCategoryDTO(filtered, false));
			}
		}
		
		Map<String, Object> model = new HashMap<>();
		model.put("categories", dtos);
		
		return new ModelAndView("/partial/product-list", model);
	}
}
