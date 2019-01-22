package client.controllers;

import common.Manager;

public class ManagerController {
	private ReaderController rc;
	private Manager manager;
	public ManagerController(ReaderController rc, Manager manager) {
		this.rc=rc;
		this.manager=manager;		
	}
}
