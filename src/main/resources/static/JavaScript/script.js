let sidebar = document.getElementById("sidebar");
let content = document.getElementById("content");

const openSideBar = (e) => {
	console.log(e);
	sidebar.classList.remove("toggleSidebar");
	content.classList.add("toggleContent");
};

const closeSideBar = () => {
	sidebar.classList.add("toggleSidebar");
	content.classList.remove("toggleContent");
};