create table VehicleType ( 
	vtname varchar2(20) not null PRIMARY KEY,
	features varchar2(20),
	wrate number,
	drate number,
	hrate number,
	wirate number,
	dirate number,
	hirate number,
	krate number,
	numAvail number
);


create table Customer (
	dLicense varchar2(20) not null PRIMARY KEY,
	name varchar2(100),
	address varchar2(100),
	cellphone varchar2(20)	
);

create table Vehicle ( 
	vid integer not null PRIMARY KEY,
	vLicense varchar2(50) not null,
	make varchar2(50),
	model varchar2(50),
	year varchar2(4),
	color varchar2(20),
	odometer integer,
	status varchar2(20),
	vtname varchar2(20) not null,
	location varchar2(50) not null, 
	city varchar2(50) not null,
	fuelType varchar2(50),
	foreign key (vtname) references VehicleType ON DELETE CASCADE
);


create table Reservation ( 
	confNo integer not null PRIMARY KEY,
	vtname varchar2(20) not null,
	dLicense varchar2(50) not null,
	fromDateTime Timestamp not null,
	toDateTime Timestamp not null, 
	location varchar2(50),
	city varchar2(50),
	foreign key (vtname) references VehicleType ON DELETE CASCADE,
	foreign key (dLicense) references Customer ON DELETE CASCADE
);

create table Rent(
	rid integer not null PRIMARY KEY,
	vid integer not null,
	dLicense varchar(50) not null,
	fromDateTime Timestamp not null,
	toDateTime Timestamp not null,
	odometer integer not null, 
	cardName varchar2(50),
	cardNo integer,
	ExpDate Timestamp, 
	confNo integer,
	foreign key (vid) references Vehicle ON DELETE CASCADE,
	foreign key (dlicense) references Customer ON DELETE CASCADE,
	foreign key (confNo) references Reservation ON DELETE CASCADE
);


create table Return ( 
	rid integer not null PRIMARY KEY,
	returnDateTime Timestamp, 
	odometer integer,
	fullTank varchar2(10),
	value number,
	foreign key (rid) references Rent ON DELETE CASCADE
);

commit;