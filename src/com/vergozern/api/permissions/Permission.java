package com.vergozern.api.permissions;

public interface Permission {
	boolean has(String id);

	default Permission and(Permission other) {
		return id -> has(id) && other.has(id);
	}

	default Permission or(Permission other) {
		return id -> has(id) || other.has(id);
	}

	default Permission xor(Permission other) {
		return id -> has(id) ^ other.has(id);
	}

	default Permission not() {
		return id -> !has(id);
	}
}
