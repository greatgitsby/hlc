{
This is a simple program that calculates nth fermat number.
}

begin
    
    variable n;
    variable answer;
    variable base;
    variable power;
    variable index;
    
    {You can set n here if you like, but do not change the base or power}
    n := 4;
    base := 2;
    power := 1;
    
    {we use what amounts to a for loop}
    index := 0;
    while index < n do
    begin
        power := power * 2;
        
        {increment the index}
        index := index + 1
    end;
    
    {reset the index to 0 to use again for the second half}
    index := 0;
    while index < power do
    begin
        base := base * 2;
        
        {increment the index}
        index := index + 1
    end;
    
    answer := base + 1;
    
    {print out our results}
    print "The ";
    print n;
    print "th Fermat Number is:  ";
    print answer;
    print "\n"
    
end
