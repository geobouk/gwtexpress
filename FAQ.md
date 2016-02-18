#### What is GWT Express? ####
> GWT Express is a simple framework that can be used to build database oriented application with very little UI coding
#### What persistence technologies are used on the server side? ####
> Simple JDBC
#### What tool-kits are used on the client side? ####
  * MyGWT - Layout, Tabs, Table etc
  * Vanilla GWT Widgets  - TextBox, Button, ListBox, SuggestBox etc.
#### Where can I find the demo? ####
> http://GWTExpress.com
#### What is the username/password for the demo application? ####
> demo/demo
#### What database does the demo application uses? ####
> MySQL
#### What application server the demo is running on? ####
> Apache Tomcat/5.5.25
#### What are the benefits of using GWTExpress framework instead of GWT? ####
> Well, GWTExpress is built using GWT & MyGWT (For layouts & tables). So most of the code you would write for developing a database oriented application is already built into the framework. You just add additional business validation logic on top of it. Some of the major benefits are listed below
  1. No need to write JDBC code for handling Create/Update/Fetch/Delete.
  1. No need to write code for handling session & login security.
  1. The framework provides built-in function based security to restrict certain operations based on roles.
  1. Populating ListBox & SugguestBox are taken care by the framework based on a lookup table. For non-lookup based, we just have to write custom SQL on the server side. Refer "How to populate ListBox? or SuggestBox? from a custom table instead of lookup table?" section in HowTo.
  1. The framework provides built-in search capability in two modes "Simple" & "Advanced".
  1. Centralized code for basic validation of numbers, dates & date times are built-in.
  1. Generic code for rendering your application page. [ExpressPage.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Client/src/com/gwtexpress/client/ui/ex/ExpressPage.java). Can easily add new features to this one class, and will get reflected to the entire application.
  1. Generic code for rendering Data entry Forms [InputFormLayout.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Client/src/com/gwtexpress/client/ui/form/InputFormLayout.java).
  1. Minimal code (small .js files)
  1. FieldHandler for custom client-side validation. Refer "How to perform custom field validation?" section in HowTo.
#### Can I use this framework to build my application? ####
> Yes.
#### Hey! I like this idea. Can I contribute? ####
> Yes. Please send an email to gwtexpress@gmail.com