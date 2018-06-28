package com.edu.uni.augsburg.uniatron.model;

/**
 * The type of the time credit.
 *
 * @author Fabio Hellmann
 */
public enum TimeCreditType {
    /**
     * This time credit will pay steps for some free minutes.
     */
    MINUTES_FOR_STEPS,
    /**
     * This time credit will pay an amount of time when all apps on
     * the blacklist will be blocked for some free minutes.
     */
    LEARNING_AID
}
