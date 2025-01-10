package com.example.test;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CountryCodeExtractor {

    public static void main(String[] args) {
        // Input: Address string
        String address = "Chicago, IL US";

        // Approach 1: Using String.split()
        String[] parts = address.split(" "); // Split the address by spaces
        String countryCodeFromSplit = parts[parts.length - 1]; // Last part is the country code
        System.out.println("Country Code using split: " + countryCodeFromSplit);

        // Approach 2: Using Regex
        String regex = "\\b[A-Z]{2}\\b$"; // Regex to match exactly two uppercase letters at the end of the string
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);

        String countryCodeFromRegex = "";
        if (matcher.find()) {
            countryCodeFromRegex = matcher.group();
        } else {
            countryCodeFromRegex = "No match found";
        }
        System.out.println("Country Code using regex: " + countryCodeFromRegex);
    }
}
