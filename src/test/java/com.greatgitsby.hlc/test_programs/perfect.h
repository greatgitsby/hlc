{
    This program checks to see if a number is a perfect number. 
    A number is considered perfect if it is equal to the sum of
    its positive divisors, excluding itself. For example, 6 is a perfect 
    number because 1 + 2 + 3 = 6.
}

begin

    { n is the number whose perfection is in question }
    variable n;

    variable sum;
    variable counter;
    variable fakeMod;

    n := 6;
    sum := 0;


    { We will use counter to count down from half of n to find all its divisors }
    counter := n / 2 + 1;

    {
      For each counter, see if it divides evenly into n, and add it to the sum
      if it is.
    }
    while counter > 0 do
    begin
        
        { We don't have mod, so we can clumsily simulate it here }
        fakeMod := counter;
        while fakeMod < n do
        begin
            fakeMod := fakeMod + counter
        end;
        if fakeMod = n then
        begin
            sum := sum + counter
        end;

        counter := counter - 1
    end;

    print n;
    if sum = n then
    begin
        print " is perfect\n"
    end
    else
    begin
        print " is not perfect\n"
    end

end
