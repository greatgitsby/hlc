{
This is a method for determining the result of raising the base number to the exponent
}

begin
	variable base;
	variable exponent;
	variable result;
	variable i;

	base := 3;
	exponent := 7;
	{
	If the exponent is 0 then the result should just be 1.
	}
	result := 1;
	i := 0;

	while i < exponent do
	begin
		result := result * base;
		i := i + 1
	end;

	print base;
	print " raised to ";
	print exponent;
	print "is: ";
	print result;
	print "\n"

end
