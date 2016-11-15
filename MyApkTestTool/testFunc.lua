function testFunc( lines )
	local len = lines[1] + 1;
	for i = 2, len, 1 
	do 
		print( "lua script : " .. lines[i] );
	end
   
	--table.getn( lines );
   
	return "test result";
end
