package com.libr.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.libr.entity.Book;
import com.libr.entity.Ticket;
import com.libr.entity.User;
import com.libr.pojos.AddBook;
import com.libr.pojos.RegisterUser;
import com.libr.pojos.Profile;
import com.libr.pojos.RaiseTicket;
import com.libr.repo.BookRepository;
import com.libr.repo.TicketRepository;
import com.libr.repo.UserRepository;
import com.libr.utils.CodeSet;
import com.libr.utils.Utilities;
@CrossOrigin(origins="http://localhost:4200")
@RestController
public class MainController {
	
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TicketRepository ticketRepository;
	
	@PostMapping("/addbook")
	public String  addbook(@RequestBody AddBook addbook) {
		if(bookRepository.findById(addbook.getIsbn()).isPresent()) {
			return addbook.getIsbn()+" is already added";
		}
		Book book = new Book();
		book.setIsbn(addbook.getIsbn());
		book.setName(addbook.getName());
		book.setAuthor(addbook.getAuthor());
		book.setCategory(addbook.getCategory());
		book.setDescription(addbook.getDescription());
		book.setAvailability("Available");
		bookRepository.save(book);
		
		return  addbook.getIsbn()+" is added in database";
	}
		
		
	@PutMapping("/updatelibbookshelf/{id}/{isbn}")
	public Map<String,Integer> updatelibbookshelf(@PathVariable String id,@PathVariable String isbn) throws JsonProcessingException {
		Map<String,Integer> libbookshelf = Utilities.generateMap(userRepository.findById(id).get().getBookshelf());
		libbookshelf.put(isbn, 1);
		User user = new User();
		user  = userRepository.findById(id).get();
		user.setBookshelf(Utilities.generateJson(libbookshelf));
		userRepository.save(user);
		return libbookshelf;
	}
		
		
	
	@GetMapping("/getbooklist")
	public List<String> getbooklist()
	{
		List<Book> list = bookRepository.findAll();
		List<String> booklist = new ArrayList<String>();
		for(Book book : list)
		{
			String s = book.getName()+" ["+book.getIsbn()+"] written by "+book.getAuthor();
			booklist.add(s);
		}
		return booklist;
	}
	
	
	@GetMapping("/getbooks")
	public List<Book> getbooks(){
		return bookRepository.findAll();
	}
	
	
	@GetMapping("/getbook/{id}")
	public Book getbook(@PathVariable String id) {
		return bookRepository.findById(id).get();
	}
	
	
	@DeleteMapping("/deletebook/{isbn}")
	public String deletebook(@PathVariable String isbn) {
		if(bookRepository.findById(isbn).isPresent()) {
			bookRepository.deleteById(isbn);
			return "Book has been successfully deleted";
		}
		return "Book is not present in the bookshelf";
	}
	
	
	@PutMapping("/updatebook/{id}")
	public String updatebook(@PathVariable String id,@RequestBody Book book) {
		
		Book existingbook = bookRepository.findById(id).orElse(book);
		existingbook.setName(book.getName());
		existingbook.setAuthor(book.getAuthor());
		existingbook.setCategory(book.getCategory());
		existingbook.setDescription(book.getDescription());
		existingbook.setAvailability(book.getAvailability());
		bookRepository.save(existingbook);
		return "Book "+book.getName()+" has been successfully updated.";
		
	}
	
	
	@GetMapping("/libbookshelf/{id}")
	public List<Book> bookshelf(@PathVariable String id){
		if(userRepository.findById(id).isPresent()) {
			
			Map<String,Integer> map = Utilities.generateMap(userRepository.findById(id).get().getBookshelf());
			List<String> bookshelf = new ArrayList<String>();
			Book book;
			for(String bookid: map.keySet())
			{				
				bookshelf.add(bookid);
			}
			List<Book> booklist= new ArrayList<>();
			for(int i=0;i<bookshelf.size();i++) {
				 book = bookRepository.findById(bookshelf.get(i)).get();
				 booklist.add(book);
			}
			return booklist;

			
			
		}
		return null;
		
	}
	
	
	@PostMapping("/register")
	public String register(@RequestBody RegisterUser adduser) {
		if(userRepository.findById(adduser.getUserid()).isPresent())
			return "user "+adduser.getUserid()+" already exists.";
		else
			{
			User user = new User();
			user.setUserid(adduser.getUserid());
			user.setPassword(adduser.getPassword());
			user.setName(adduser.getName());
			user.setContact(adduser.getContact());
			user.setAddress(adduser.getContact());
			user.setBookshelf("");
			userRepository.save(user);
			return "user "+adduser.getUserid()+" successfully registered";
			}
	}
	
	
	
	@GetMapping("/login/{id}")
	public User login(@PathVariable String id) {
		return userRepository.findById(id).get();
	}
	
	
	@GetMapping("/getuserprofile/{id}")
	public Profile getuser(@PathVariable String id) {
		User user = userRepository.findById(id).get();
		Profile profile = new Profile();
		profile.setName(user.getName());
		profile.setContact(user.getContact());
		profile.setAddress(user.getAddress());
		profile.setBookshelf(user.getBookshelf());
		
		return profile;
	}
	
	
	@PostMapping("/raiseticket")
	public String raiseticket(@RequestBody RaiseTicket raiseticket) throws JsonProcessingException {
		if(userRepository.findById(raiseticket.getSender()).isPresent() && userRepository.findById(raiseticket.getSender()).get().getPassword().contentEquals(raiseticket.getAuthkey()) && userRepository.findById(raiseticket.getReceiver()).isPresent())
		{
			Ticket ticket = new Ticket();
			ticket.setSender(raiseticket.getSender());
			ticket.setReceiver(raiseticket.getReceiver());
			ticket.setStatus(CodeSet.REQUESTED);
			ticket.setIsbn(raiseticket.getIsbn());
			ticket.setDescription("Sender "+ticket.getSender()+" has successfully requested to Receiver "+ticket.getReceiver()+" for Book "+ticket.getIsbn());
			
			Map<String,String> readerbook = Utilities.generateStringMap(userRepository.findById(raiseticket.getSender()).get().getBookshelf());
			
			readerbook.put(raiseticket.getIsbn(), raiseticket.getReceiver());
			
			User user = userRepository.findById(raiseticket.getSender()).get();
			user.setBookshelf(Utilities.generateJson1(readerbook));
			ticketRepository.save(ticket);
			
			return ticket.getDescription();
		}
		return "Users not present or authenticated.";
	}
	
	
	@GetMapping("/userbookshelf/{id}")
	public List<String> userbookshelf(@PathVariable String id){
		if(userRepository.findById(id).isPresent()) {
			Map<String,String> map = Utilities.generateStringMap(userRepository.findById(id).get().getBookshelf());
			List<String> ubookshelf = new ArrayList<>();
			for(String bookid:map.keySet()) {
				Book book = bookRepository.findById(bookid).get();
				//String st = ticketRepository.findById("1").get().getStatus();
				String show = bookid+" ["+book.getName()+"] to "+map.get(bookid) +"  Status: ";
				ubookshelf.add(show);
				
			}
			return ubookshelf;
		}
		
			return new ArrayList<String>();
		
	}
	
	
	
	
	
	
	
	
	 
		
	
	
	
}
