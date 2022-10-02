package fr.iiztp.anbs.utils;

/**
 * Enum of modes for the radio
 * @author iiztp
 * @version 1.0.1
 */
public enum Mode
{
	REGION((byte) 3),
	COMBAT((byte) 2),
	RADIO((byte) 1);
	
	private byte priority;
	
	Mode(byte priority)
	{
		this.priority = priority;
	}
	
	/**
	 * Checks if a mode has priority over another
	 * @param mode : The argument to check the priority
	 * @return true if the mode has priority over the mode put in argument
	 */
	public boolean hasPriorityOver(Mode mode)
	{
		return (this.priority < mode.priority);
	}
}
