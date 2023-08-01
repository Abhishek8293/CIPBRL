package com.libr.utils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public interface Utilities {
	
	
	
	
	static Map<String,Integer> generateMap(String jsonString)
	{
		Map<String, Integer> map=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			map = mapper.readValue(jsonString, Map.class);
			return map;
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	static Map<String,String> generateStringMap(String jsonString){
		Map<String,String> map = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			map = mapper.readValue(jsonString, Map.class);
			return map;
		}catch(JsonMappingException e) {
			e.printStackTrace();
		}catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	static String generateJson(Map<String,Integer> map) throws JsonProcessingException
	{
	ObjectMapper objectMapper = new ObjectMapper();
	
	return objectMapper.writeValueAsString(map);
	}
	
	
	static String generateJson1(Map<String,String> map) throws JsonProcessingException
	{
	ObjectMapper objectMapper = new ObjectMapper();
	
	return objectMapper.writeValueAsString(map);
	}
	
}
