SET SERVEROUTPUT ON;

-- Creating our new table, after which we import data from the online dataset
create table charities (
    id varchar(1000) not null,
    name varchar(200) not null,
    motto varchar(200) not null,
    category varchar(100) not null,
    description varchar(2000) not null,
    score number not null,
    total_expenses number not null,
    leader varchar(100) not null,
    leader_compensation number
)

-- Add a primary key constraint to the id
alter table charities
add constraint pk_charities
primary key (id)

-- Add two new visual graphs
alter table charities
add (
    charity_size as (
        case
        when total_expenses <= 3500000 then 'Small'
        when total_expenses > 3500000 and total_expenses <= 13500000 then 'Medium'
        when total_expenses > 13500000 then 'Big'
        end
    )
)

alter table charities
add (
    leader_compensation_percentage as (
        leader_compensation / total_expenses
    )
)

-- Add an image column
alter table charities 
add logo_image varchar(1000)

-- Fill the image columns
update charities
set logo_image = 'https://blogmedia.evbstatic.com/wp-content/uploads/wpmulti/sites/8/2019/10/Promote-charity-event-1200x630.png'
where id = '93-0642086'

update charities
set logo_image = 'https://image.freepik.com/free-vector/hand-people-care-logo-vector_23987-249.jpg'
where id = '31-1770828'

update charities
set logo_image = 'https://image.shutterstock.com/image-vector/hand-care-logo-template-icon-260nw-1067682956.jpg'
where id = '91-1857425'

update charities
set logo_image = 'https://static9.depositphotos.com/1364916/1091/v/600/depositphotos_10914951-stock-illustration-teamwork-charity-logo-vector.jpg'
where id = '51-0145980'

update charities
set logo_image = 'https://thumbs.dreamstime.com/z/hand-charity-logo-template-vector-icon-illustration-design-171962373.jpg'
where id = '22-2579809'

update charities
set logo_image = 'https://cdn4.vectorstock.com/i/thumb-large/02/88/my-planet-hands-embracing-symbol-logo-vector-22410288.jpg'
where id = '15-0623468'

update charities
set logo_image = 'https://i.pinimg.com/474x/20/5b/8e/205b8e695a7da8224a6982b6432a6110.jpg'
where id = '95-6006642'

update charities
set logo_image = 'https://www.usnews.com/dims4/USNEWS/9b62b7c/2147483647/crop/2000x1313%2B0%2B4/resize/640x420/quality/85/?url=http%3A%2F%2Fmedia.beam.usnews.com%2Fa9%2Fe8%2F046d240745f590f48c4d6067a9f5%2F200923-stock.jpg'
where id = '26-2176362'

update charities
set logo_image = 'https://econsultancy.imgix.net/content/uploads/2018/01/05151122/ROW-50-charity.png'
where id = '43-1426384'

update charities
set logo_image = 'https://www.moonstone.co.za/upmedia/uploads/charity.png'
where id = '13-3471084'

update charities
set logo_image = 'https://i.pinimg.com/474x/20/5b/8e/205b8e695a7da8224a6982b6432a6110.jpg'
where id <> '93-0642086' and id <> '31-1770828' and id <> '91-1857425' 
and id <> '51-0145980' and id <> '22-2579809' and id <> '15-0623468'
and id <> '95-6006642' and id <> '26-2176362' and id <> '43-1426384'
and id <> '13-3471084'

-- Package for sorting, which has two procedures, sorting by score and leader compensation
-- Package specification
create or replace package sorting_procedures
is
    procedure sort_by_score(p_recordset out sys_refcursor);
    procedure sort_by_lc(p_recordset out sys_refcursor);
end sorting_procedures;

-- Package body
create or replace package body sorting_procedures
is
    procedure sort_by_score(p_recordset out sys_refcursor)
    is
    type charity_type is table of charities%ROWTYPE;
    char_rec charity_type;
    begin
        open p_recordset for 
        select * from charities
        order by score desc;
    end sort_by_score;
    
    procedure sort_by_lc(p_recordset out sys_refcursor)
    is
    type charity_type is table of charities%ROWTYPE;
    char_rec charity_type;
    begin
        open p_recordset for 
        select * from charities
        order by leader_compensation desc;
    end sort_by_lc;
end;

-- Package for grouping, which has two procedures, grouping by category and size
-- Package specification
create or replace package grouping_procedures
is
    procedure groupByCategory(p_recordset out sys_refcursor);
    procedure groupBySize(p_recordset out sys_refcursor);
end grouping_procedures;

-- Package body
create or replace package body grouping_procedures
is
    procedure groupByCategory(p_recordset out sys_refcursor)
    is 
    type charity_type is table of charities%ROWTYPE;
    char_rec charity_type;
    begin
        open p_recordset for 
        select * from charities
        order by category;
    end groupByCategory;
    
    procedure groupBySize(p_recordset out sys_refcursor)
    is 
    type charity_type is table of charities%ROWTYPE;
    char_rec charity_type;
    begin
        open p_recordset for 
        select * from charities
        order by charity_size;
    end groupBySize;
end grouping_procedures;

-- Log table 
create table charities_log (
    id number,
    operation_date date,
    action varchar(25),
    action_author varchar(50),
    old_id varchar2(1000),
    new_id varchar2(1000),
    old_name varchar2(200),
    new_name varchar2(200),
    old_motto varchar2(200),
    new_motto varchar2(200),
    old_category varchar2(100),
    new_category varchar2(100),
    old_description varchar2(2000),
    new_description varchar2(2000),
    old_score number,
    new_score number,
    old_total_expenses number,
    new_total_expenses number,
    old_leader varchar2(100),
    new_leader varchar2(100),
    old_leader_comp number,
    new_leader_comp number,
    old_charity_size varchar2(6),
    new_charity_size varchar2(6),
    old_leader_comp_percntg number,
    new_leader_comp_percntg number,
    old_logo_image varchar2(1000),
    new_logo_image varchar2(1000)
)

-- Trigger after delete on charities, which adds a new entry to the log table. Also uses Dynamic SQL
create or replace trigger after_delete_charities
after delete on charities 
for each row
declare
max_id number;
plsql_block varchar(2000);
begin
    select count(*) + 1 into max_id from charities_log;
    plsql_block := 'insert into charities_log (id, operation_date, action, action_author, old_id,
    old_name, old_motto, old_category, old_description, old_score, old_total_expenses,
    old_leader, old_leader_comp, old_charity_size, old_leader_comp_percntg, old_logo_image)
    values(' || max_id || ', ''' || sysdate || ''', ''DELETE'', ''SYSTEM'', ''' || :OLD.id || ''', ''' || :OLD.name || ''', ''' || :OLD.motto || ''', '''
    || :OLD.category || ''', ''' || :OLD.description || ''', ' || :OLD.score || ', ' || :OLD.total_expenses || ', ''' || :OLD.leader || ''', '
    || :OLD.leader_compensation || ', ''' || :OLD.charity_size || ''', ' || :OLD.leader_compensation_percentage || ', '''
    || :OLD.logo_image || ''')';
    execute immediate plsql_block;
end;

-- Trigger after insert on charities, which adds a new entry to the log table. Also uses Dynamic SQL
create or replace trigger after_insert_charities
after insert on charities 
for each row
declare
max_id number;
plsql_block varchar(2000);
begin
    select count(*) + 1 into max_id from charities_log;
    plsql_block := 'insert into charities_log (id, operation_date, action, action_author, new_id,
    new_name, new_motto, new_category, new_description, new_score, new_total_expenses,
    new_leader, new_leader_comp, new_charity_size, new_leader_comp_percntg, new_logo_image)
    values(' || max_id || ', ''' || sysdate || ''', ''INSERT'', ''SYSTEM'', ''' || :NEW.id || ''', ''' || :NEW.name || ''', ''' || :NEW.motto || ''', '''
    || :NEW.category || ''', ''' || :OLD.description || ''', ' || :NEW.score || ', ' || :NEW.total_expenses || ', ''' || :NEW.leader || ''', '
    || :NEW.leader_compensation || ', ''' || :NEW.charity_size || ''', ' || :NEW.leader_compensation_percentage || ', '''
    || :NEW.logo_image || ''')';
    execute immediate plsql_block;
end;

-- Trigger after update on charities, which adds a new entry to the log table. Also uses Dynamic SQL
create or replace trigger after_update_charities
after update on charities 
for each row
declare
max_id number;
plsql_block varchar(3000);
begin
    select count(*) + 1 into max_id from charities_log;
    plsql_block := 'insert into charities_log 
    values(' || max_id || ', ''' || sysdate || ''', ''UPDATE'', ''SYSTEM'', ''' || :OLD.id || ''', ''' || :NEW.id || ''', ''' ||
    :OLD.name || ''', ''' || :NEW.name || ''', ''' || :OLD.motto || ''', ''' || :NEW.motto || ''', ''' || 
    :OLD.category || ''', ''' || :NEW.category || ''', ''' || :OLD.description || ''', ''' || :NEW.description || ''', ' ||
    :OLD.score || ', ' || :NEW.score || ', ' || :OLD.total_expenses || ', ' || :NEW.total_expenses || ', ''' || :OLD.leader || 
    ''', ''' || :NEW.leader || ''', ' || :OLD.leader_compensation || ', ' || :NEW.leader_compensation || ', ''' || :OLD.charity_size ||
    ''', ''' || :NEW.charity_size || ''', ' || :OLD.leader_compensation_percentage || ', ' || 
    :NEW.leader_compensation_percentage|| ', ''' || :OLD.logo_image || ''', ''' || :NEW.logo_image || ''')';
    DBMS_OUTPUT.PUT_LINE(plsql_block);
    execute immediate plsql_block;
end;