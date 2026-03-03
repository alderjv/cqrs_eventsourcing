// src/main/java/br/org/postalis/training/rh/shared/application/BusinessRuleException.java
package br.org.postalis.training.rh.shared.domain;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}