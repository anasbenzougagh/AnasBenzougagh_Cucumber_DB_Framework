select * from Employees;

select LastName,FirstName,Title,TitleOfCourtesy,Address,HomePhone,Notes from Employees
where LastName = 'Leverling';


select * from Products;

select ProductName , QuantityPerUnit, UnitPrice,UnitsInStock,ReorderLevel from Products
where ProductName = 'Chai';