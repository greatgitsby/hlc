{ Fibonacci's method for generating infinitely many pythagorean triples. This will generate the first 5 triples based on the
sequence starting with 1, 4. The expected result is:

Triple #1: [24, 7, 25]
Triple #2: [56, 33, 65]
Triple #3: [154, 72, 170]
Triple #4: [396, 203, 445]
Triple #5: [1044, 517, 1165] 
}

begin
		
    { variable declaration }
    variable fib_1;
    variable fib_2;
    variable fib_3;
    variable fib_4;
    variable count;
    variable a;
    variable b;
    variable c;
    variable temp;
		
    { set initial fibonacci values }
    fib_1 := 1;
    fib_2 := 3;
    fib_3 := fib_1 + fib_2;
    fib_4 := fib_2 + fib_3;
		
    { initialize count }
    count := 0;
		
    { set loop to find 5 pythagorean triples }
    while count < 5 do
    begin

        { calculate a = 2 * fib2 * fib3 }
	a := fib_2 * fib_3;
	a := 2 * a;
			
	{ calculate b = fib1 * fib4 }
	b := fib_1 * fib_4;
			
	{ calculate c = fib3 * fib4 - fib1 * fib2 }
	temp := fib_1 * fib_2;
	c := fib_3 * fib_4;
        c := c - temp;
			
	{ increment count }
	count := count + 1;
			
	{ reset fibonacci values }
	fib_1 := fib_2;
	fib_2 := fib_3;
	fib_3 := fib_4;
	fib_4 := fib_2 + fib_3;
			
	print "Triple #";
	print count;
	print ": [";
	print a;
	print ", ";
        print b;
	print ", ";
	print c;
	print"]\n"	
    end
end