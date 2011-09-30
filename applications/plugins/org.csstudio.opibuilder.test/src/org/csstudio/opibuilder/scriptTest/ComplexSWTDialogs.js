importPackage(Packages.org.eclipse.swt);
importPackage(Packages.org.eclipse.swt.widgets);
importPackage(Packages.org.eclipse.swt.events);
importPackage(Packages.org.eclipse.swt.layout);
importPackage(Packages.org.eclipse.jface.dialogs);
importPackage(Packages.java.lang);


		//var display = new Display();
		var shell = new Shell();
		shell.setSize(465, 200);
		shell.setText("MessageDialog");
		shell.setLayout(new GridLayout(5, false));
		text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		var data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 5;
		text.setLayoutData(data);
		
		confirm = new Button(shell, SWT.NONE);
		confirm.setText("Confirm");
		gridconfirm = new GridData();
		gridconfirm.widthHint = 85;
		gridconfirm.heightHint = 25;
		confirm.setLayoutData(gridconfirm);

		var information = new Button(shell, SWT.NONE);
		information.setText("Information");
		var gridinformation = new GridData();
		gridinformation.widthHint = 85;
		gridinformation.heightHint = 25;
		information.setLayoutData(gridinformation);
	
		listener = {
			widgetSelected:function(event) 
			{
				text.setText(x.getX());
				var op = MessageDialog.openInformation(null, "Information", text.getText());

			}
		};
		alistener = new SelectionListener(listener);
		information.addSelectionListener(alistener);
/*
		var question = new Button(shell, SWT.NONE);
		question.setText("Question");
		var gridquestion = new GridData();
		gridquestion.widthHint = 85;
		gridquestion.heightHint = 25;
		question.setLayoutData(gridquestion);
		question.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean b = MessageDialog.openQuestion(shell, "Question", text
						.getText());
				label.setText("Returned " + Boolean.toString(b));
			}
		});

		var error = new Button(shell, SWT.NONE);
		error.setText("Error");
		var griderror = new GridData();
		griderror.widthHint = 85;
		griderror.heightHint = 25;
		error.setLayoutData(griderror);
		error.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				MessageDialog.openError(shell, "Error", text.getText());
				label.setText("product error information");
			}
		});

		var warning = new Button(shell, SWT.NONE);
		warning.setText("Warning");
		var gridwarning = new GridData();
		gridwarning.widthHint = 85;
		gridwarning.heightHint = 25;
		warning.setLayoutData(gridwarning);
		warning.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				MessageDialog.openWarning(shell, "Warning", text.getText());
				label.setText("product waring information");
			}
		});

		label = new Label(shell, SWT.NONE);
		label.setText("It is not Information");
		var datalabel = new GridData(GridData.FILL_HORIZONTAL);
		datalabel.horizontalSpan = 5;
		datalabel.heightHint = 20;
		label.setLayoutData(datalabel);
	*/
		shell.open();
		shell.layout();
	/*	while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		*/
		//display.dispose();


  