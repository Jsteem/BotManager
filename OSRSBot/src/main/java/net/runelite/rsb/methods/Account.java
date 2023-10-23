package net.runelite.rsb.methods;

import lombok.Getter;
import lombok.Setter;
import net.runelite.rsb.plugin.AccountManager;

import java.util.logging.Logger;

/**
 * Selected account information.
 */
@Getter
@Setter

public class Account extends MethodProvider {

	Logger log = Logger.getLogger(getClass().getName());

	public Account(MethodContext ctx) {
		super(ctx);
	}


	String login;
	String password;
	Boolean isMember;
	String startScript;
}
