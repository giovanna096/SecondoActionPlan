/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016, 2017 FENECON GmbH and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   FENECON GmbH - initial API and implementation and initial documentation
 *******************************************************************************/
package io.openems.core.utilities;

import java.net.Inet4Address;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.openems.api.exception.NotImplementedException;
import io.openems.api.exception.ReflectionException;

interface Handler<T extends Object> {
	  Handler<T> returnPrim(T o);
	}


private class NumbHandler implements Handler<Number>{
	
	public JsonElement returnPrim(Number value){
		return new JsonPrimitive((Number) value);
	}
}

private class StringHandler implements Handler<String>{
	
	public JsonElement returnPrim(String value){
		return new JsonPrimitive((String) value);
	}
}


private class BooleanHandler implements Handler<Boolean>{
	
	public JsonElement returnPrim(Boolean value){
		return new JsonPrimitive((Boolean) value);
	}
}

private class Inet4AddressHandler implements Handler<Inet4Address>{
	
	public JsonElement returnPrim(Inet4Address value){
		return new JsonPrimitive((Inet4Address) value);
	}
}

private class JsonElementHandler implements Handler<JsonElement>{
	
	public JsonElement returnPrim(JsonElement value){
		return (JsonElement) value;
	}
}

private class LongHandler implements Handler<Long[]>{
	
	public JsonElement returnPrim(Long[] value){
		
		JsonArray js = new JsonArray();
		for (Long l : (Long[]) value){
			js.add(new JsonPrimitive((Long) l));
		}
		return js;
	}
}


/**
 *
 * @author FENECON GmbH
 *
 */
public class JsonUtils {
	public static JsonArray getAsJsonArray(JsonElement jElement) throws ReflectionException {
		if (!jElement.isJsonArray()) {
			throw new ReflectionException("Config is not a JsonArray: " + jElement);
		}
		return jElement.getAsJsonArray();
	};

	public static JsonArray getAsJsonArray(JsonElement jElement, String memberName) throws ReflectionException {
		JsonElement jSubElement = getSubElement(jElement, memberName);
		if (!jSubElement.isJsonArray()) {
			throw new ReflectionException("Config [" + memberName + "] is not a JsonArray: " + jSubElement);
		}
		return jSubElement.getAsJsonArray();
	};

	public static JsonObject getAsJsonObject(JsonElement jElement) throws ReflectionException {
		if (!jElement.isJsonObject()) {
			throw new ReflectionException("Config is not a JsonObject: " + jElement);
		}
		return jElement.getAsJsonObject();
	};

	public static JsonObject getAsJsonObject(JsonElement jElement, String memberName) throws ReflectionException {
		JsonElement jsubElement = getSubElement(jElement, memberName);
		if (!jsubElement.isJsonObject()) {
			throw new ReflectionException("Config is not a JsonObject: " + jsubElement);
		}
		return jsubElement.getAsJsonObject();
	};

	public static JsonPrimitive getAsPrimitive(JsonElement jElement) throws ReflectionException {
		if (!jElement.isJsonPrimitive()) {
			throw new ReflectionException("Config is not a JsonPrimitive: " + jElement);
		}
		return jElement.getAsJsonPrimitive();
	}

	public static JsonPrimitive getAsPrimitive(JsonElement jElement, String memberName) throws ReflectionException {
		JsonElement jSubElement = getSubElement(jElement, memberName);
		if (!jSubElement.isJsonPrimitive()) {
			throw new ReflectionException("Config is not a JsonPrimitive: " + jSubElement);
		}
		return jSubElement.getAsJsonPrimitive();
	}

	public static String getAsString(JsonElement jElement) throws ReflectionException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement);
		if (!jPrimitive.isString()) {
			throw new ReflectionException("Config is not a String: " + jPrimitive);
		}
		return jPrimitive.getAsString();
	}

	public static String getAsString(JsonElement jElement, String memberName) throws ReflectionException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (!jPrimitive.isString()) {
			throw new ReflectionException("[" + memberName + "] is not a String: " + jPrimitive);
		}
		return jPrimitive.getAsString();
	}

	public static int getAsInt(JsonElement jElement, String memberName) throws ReflectionException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (jPrimitive.isNumber()) {
			return jPrimitive.getAsInt();
		} else if (jPrimitive.isString()) {
			String string = jPrimitive.getAsString();
			return Integer.parseInt(string);
		}
		throw new ReflectionException("[" + memberName + "] is not a Number: " + jPrimitive);
	}

	public static ZonedDateTime getAsZonedDateTime(JsonElement jElement, String memberName, ZoneId timezone)
			throws ReflectionException {
		String[] date = JsonUtils.getAsString(jElement, memberName).split("-");
		try {
			int year = Integer.valueOf(date[0]);
			int month = Integer.valueOf(date[1]);
			int day = Integer.valueOf(date[2]);
			return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, timezone);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ReflectionException("Unable to parse date [" + memberName + "] from [" + jElement + "]: " + e);
		}
	}

	public static long getAsLong(JsonElement jElement, String memberName) throws ReflectionException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (jPrimitive.isNumber()) {
			return jPrimitive.getAsLong();
		} else if (jPrimitive.isString()) {
			String string = jPrimitive.getAsString();
			return Long.parseLong(string);
		}
		throw new ReflectionException("[" + memberName + "] is not a Number: " + jPrimitive);
	}

	public static boolean getAsBoolean(JsonElement jElement, String memberName) throws ReflectionException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (jPrimitive.isBoolean()) {
			return jPrimitive.getAsBoolean();
		}
		throw new ReflectionException("[" + memberName + "] is not a Boolean: " + jPrimitive);
	}

	public static JsonElement getSubElement(JsonElement jElement, String memberName) throws ReflectionException {
		JsonObject jObject = getAsJsonObject(jElement);
		if (!jObject.has(memberName)) {
			throw new ReflectionException("[" + memberName + "] is missing: " + jElement);
		}
		return jObject.get(memberName);
	}

	public static JsonElement getAsJsonElement(Object value) throws NotImplementedException {
		// null
		if (value == null) {
			return null;
		}
		// optional
		if (value instanceof Optional<?>) {
			if (!((Optional<?>) value).isPresent()) {
				return null;
			} else {
				value = ((Optional<?>) value).get();
			}
		}
		
		Map<Class, Handler> handlers = new HashMap<Class, Handler>();
		handlers.put(Number.class, new Number());
		handlers.put(String.class, new String());
		handlers.put(Boolean.class, new Boolean());
		handlers.put(Inet4Address.class, new Inet4Address());
		handlers.put(JsonElement.class, new JsonElement());
		handlers.put(Long[].class, new Long());
		
		//funzione
		if(returnCorrect(value)){
			return returnElement(value);
		}
		
		throw new NotImplementedException("Converter for [" + value + "]" + " of type [" //
				+ value.getClass().getSimpleName() + "]" //
				+ " to JSON is not implemented.");
	}
	
	
	/*
	 * Return true if the value is of a type implemented
	 */
	public boolean returnCorrect(Object value){
		
		Handler h = handlers.get(value.getClass());
		if(h != null) return true;
		else return false;
	}
	
	/*
	 * Check what the value is e return the right instance
	 */
	public JsonElement returnElement(Object value){
		
		Handler h = handlers.get(value.getClass());
		if(h != null) return h.returnPrim(value);
		else return null;
		
	}

	public static Optional<Class<?>> getAsType(Optional<Class<?>> typeOptional, JsonElement j) throws NotImplementedException {
		if (!typeOptional.isPresent()) {
			throw new NotImplementedException("Type of Channel was not set: " + j.getAsString());
		}
		Class<?> type = typeOptional.get();
		return getAsType(type, j);
	}

	public static Class<?> getAsType(Class<?> type, JsonElement j) throws NotImplementedException {
		try {
			
			//funzione
			if(returnTrue(type)){
				return returnObj();
			}
			
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Failed to parse JsonElement [" + j + "]", e);
		}
		throw new NotImplementedException(
				"Converter for value [" + j + "] to class type [" + type + "] is not implemented.");
	}
	
	/*
	 * Check if there is a value to return
	 */
	public boolean returnTrue(Class<?> type){
		
		if (Integer.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) || JsonObject.class.isAssignableFrom(type) || JsonArray.class.isAssignableFrom(type) || type.isArray()) {
			/*
			 * Asking for an Integer
			 */
			return true;

		}  
		
		return false;
		
	}
	
	/*
	 * Check what is the value to return
	 */
	public Class<?> returnObj(Class<?> type, JsonElement j){
		
		if (Integer.class.isAssignableFrom(type)) {
			/*
			 * Asking for an Integer
			 */
			return j.getAsInt();

		} else if (Long.class.isAssignableFrom(type)) {
			/*
			 * Asking for an Long
			 */
			return j.getAsLong();
		} else if (Boolean.class.isAssignableFrom(type)) {
			/*
			 * Asking for an Boolean
			 */
			return j.getAsBoolean();
		} else if (Double.class.isAssignableFrom(type)) {
			/*
			 * Asking for an Double
			 */
			return j.getAsDouble();
		} else {
			
			checkRest(type, j);
		}
		
	}
	
	public Class<?> checkRest(Class<?> type, JsonElement j){
		
		if (String.class.isAssignableFrom(type)) {
			/*
			 * Asking for a String
			 */
			return j.getAsString();
		} else if (JsonObject.class.isAssignableFrom(type)) {
			/*
			 * Asking for a JsonObject
			 */
			return j.getAsJsonObject();
		} else if (JsonArray.class.isAssignableFrom(type)) {
			/*
			 * Asking for a JsonArray
			 */
			return j.getAsJsonArray();
		} else if (type.isArray()){
			
			//funzione
			return arrOfLong(type, j);

		}
	}
	
	/*
	 * Get the array of long
	 */
	public long arrOfLong(Class<?> type, JsonElement j){
		/**
		 * Asking for Array
		 */
		if(Long.class.isAssignableFrom(type.getComponentType())){
			/**
			 * Asking for ArrayOfLong
			 */
			if(j.isJsonArray()){
				JsonArray js = j.getAsJsonArray();
				Long[] la = new Long[js.size()];
				int sizeJs= js.size();
				for(int i = 0; i < sizeJs; i++){
					la[i] = js.get(i).getAsLong();
				}
				return la;
			}

		}
	}

	public static boolean hasElement(JsonElement j, String... paths) {
		return getMatchingElements(j, paths).size() > 0;
	}

	public static Set<JsonElement> getMatchingElements(JsonElement j, String... paths) {
		Set<JsonElement> result = new HashSet<JsonElement>();
		if (paths.length == 0) {
			// last path element
			result.add(j);
			return result;
		}
		String path = paths[0];
		if (j.isJsonObject()) {
			JsonObject jO = j.getAsJsonObject();
			if (jO.has(path)) {
				List<String> nextPathsList = new ArrayList<String>(Arrays.asList(paths));
				nextPathsList.remove(0);
				String[] nextPaths = nextPathsList.toArray(new String[0]);
				result.addAll(getMatchingElements(jO.get(path), nextPaths));
			}
		} else if (j.isJsonArray()) {
			for (JsonElement jE : j.getAsJsonArray()) {
				result.addAll(getMatchingElements(jE, paths));
			}
		} else if (j.isJsonPrimitive()) {
			JsonPrimitive jP = j.getAsJsonPrimitive();
			
			//funzione
			result = checkPrimitive(result, jP);
			
		}
		return result;
	}
	
	/*
	 * Check if jP is a string and is equals to path
	 */
	public Set<JsonElement> checkPrimitive(Set<JsonElement> result, JsonPrimitive jP ){
		
		if (jP.isString()) {
			if (jP.getAsString().equals(path)) {
				result.add(jP);
			}
		}
		
		return result;
		
	}

	/**
	 * Pretty print a JsonElement
	 *
	 * @param j
	 */
	public static void prettyPrint(JsonElement j) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(j);
		System.out.println(json);
	}
}