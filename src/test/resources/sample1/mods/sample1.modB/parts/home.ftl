<h3>Create Employee</h3>
<form method="POST">
	<input type="hidden" name="action" value="sample1.modB:saveEmployee" />
User Name:
<input type="text" name="username" value=""/>
<input type="submit" value="save" />
</form>
[#if m.employees??]
<ul>
	[#list m.employees as e]
	<li>${e.username}</li>
	[/#list]
</ul>
[/#if]