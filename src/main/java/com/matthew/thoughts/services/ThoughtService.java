package com.matthew.thoughts.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matthew.thoughts.models.Thought;
import com.matthew.thoughts.models.User;
import com.matthew.thoughts.repositories.ThoughtRepository;

@Service
public class ThoughtService {
	@Autowired
	private ThoughtRepository tRepo;
	
	public List<Thought> getThoughts(){
		return this.tRepo.findAll();
	}
	
	public Thought getById(Long id) {
		return this.tRepo.findById(id).orElse(null);
	}
	
	public Thought create(Thought thought) {
		return this.tRepo.save(thought);
	}
	
	public void delete(Long id) {
		this.tRepo.deleteById(id);
	}
	
	public void likeThought(Thought thought, User user) {
		//Get the list of likers from thought
		List<User> allTheLikers = thought.getLikers();
		allTheLikers.add(user);
		this.tRepo.save(thought);
	}
	public void unlikeThought(Thought thought, User user) {
		//Get the list of likers from thought
		List<User> allTheLikers = thought.getLikers();
		allTheLikers.remove(user);
		this.tRepo.save(thought);
	}
}
